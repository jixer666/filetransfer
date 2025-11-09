/**
 * 分片上传工具类（支持Redis断点续传）
 */
class ChunkedUpload {
  constructor(file, options = {}) {
    this.file = file;
    this.chunkSize = options.chunkSize || 1024 * 1024; // 默认1MB
    this.totalChunks = Math.ceil(file.size / this.chunkSize);
    this.identifier = this.generateIdentifier(file);
    this.currentChunk = 0;
    this.uploadedChunks = new Set();
    this.paused = false;
    this.completed = false;
    this.uploadedSize = 0;
    this.totalSize = file.size;
  }

  /**
   * 生成文件唯一标识
   */
  generateIdentifier(file) {
    return `${file.name}-${file.size}-${file.lastModified}`;
  }

  /**
   * 检查分片是否已上传
   */
  async checkChunk(chunkNumber) {
    try {
      const response = await fetch(`http://localhost:8080/api/chunked/check?chunkNumber=${chunkNumber}&identifier=${this.identifier}`);
      const result = await response.json();
      return result.exists;
    } catch (error) {
      console.error('检查分片失败:', error);
      return false;
    }
  }

  /**
   * 获取上传进度
   */
  async getProgress() {
    try {
      const response = await fetch(`http://localhost:8080/api/chunked/progress?identifier=${this.identifier}`);
      const result = await response.json();
      if (result.success) {
        return {
          progress: result.progress,
          uploadedSize: result.uploadedSize,
          totalSize: result.totalSize,
          uploadedChunks: result.uploadedChunks
        };
      }
    } catch (error) {
      console.error('获取进度失败:', error);
    }
    return null;
  }

  /**
   * 上传单个分片
   */
  async uploadChunk(chunkNumber) {
    const start = (chunkNumber - 1) * this.chunkSize;
    const end = Math.min(start + this.chunkSize, this.file.size);
    const chunk = this.file.slice(start, end);

    const formData = new FormData();
    formData.append('chunk', chunk);
    formData.append('chunkNumber', chunkNumber);
    formData.append('totalChunks', this.totalChunks);
    formData.append('filename', this.file.name);
    formData.append('fileSize', this.file.size);
    formData.append('identifier', this.identifier);

    try {
      const response = await fetch('http://localhost:8080/api/chunked/upload', {
        method: 'POST',
        body: formData
      });

      const result = await response.json();
      if (result.success) {
        this.uploadedChunks.add(chunkNumber);
        if (result.uploadedSize) {
          this.uploadedSize = result.uploadedSize;
        }
        return { success: true, chunkNumber, result };
      } else {
        throw new Error(result.message);
      }
    } catch (error) {
      console.error('上传分片失败:', error);
      return { success: false, chunkNumber, error };
    }
  }

  /**
   * 上传下一个分片
   */
  async uploadNextChunk() {
    if (this.paused || this.completed) {
      return;
    }

    // 查找下一个未上传的分片
    let nextChunk = 1;
    while (nextChunk <= this.totalChunks && this.uploadedChunks.has(nextChunk)) {
      nextChunk++;
    }

    if (nextChunk > this.totalChunks) {
      this.completed = true;
      return { success: true, completed: true };
    }

    // 检查分片是否已上传
    const exists = await this.checkChunk(nextChunk);
    if (exists) {
      this.uploadedChunks.add(nextChunk);
      return this.uploadNextChunk();
    }

    // 上传分片
    const result = await this.uploadChunk(nextChunk);
    return result;
  }

  /**
   * 开始上传
   */
  async start() {
    this.paused = false;
    this.completed = false;

    // 获取之前的上传进度
    const progress = await this.getProgress();
    if (progress && progress.uploadedChunks) {
      progress.uploadedChunks.forEach(chunk => {
        this.uploadedChunks.add(chunk);
      });
      this.uploadedSize = progress.uploadedSize;
    }

    // 上传所有分片
    while (!this.completed && !this.paused) {
      const result = await this.uploadNextChunk();
      if (result && result.completed) {
        break;
      }
      
      // 通知进度更新
      if (this.onProgress) {
        const currentProgress = await this.getProgress();
        if (currentProgress) {
          this.onProgress(currentProgress);
        }
      }
    }
    
    return { completed: this.completed };
  }

  /**
   * 暂停上传
   */
  pause() {
    this.paused = true;
  }

  /**
   * 恢复上传
   */
  resume() {
    this.paused = false;
    return this.start();
  }

  /**
   * 获取上传进度百分比
   */
  getProgressPercentage() {
    if (this.totalSize === 0) return 0;
    return (this.uploadedSize / this.totalSize) * 100;
  }

  /**
   * 设置进度回调
   */
  setOnProgress(callback) {
    this.onProgress = callback;
  }
}

export default ChunkedUpload;