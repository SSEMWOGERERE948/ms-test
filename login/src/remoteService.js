import axios from 'axios';
import { getConfig } from './config';
class RemoteService {
  constructor() {
    this.BASE_URL = getConfig().BASE_URL;
  }

  getCurrentToken() {
    const token = localStorage.getItem('token');
    return token ? `Bearer ${token}` : null;
  }

  async sendPostToServer(url, requestData) {
    const token = this.getCurrentToken();

    const httpOptions = {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        Authorization: token, 
      },
    };

    return await axios.post(`${this.BASE_URL}${url}`, requestData, httpOptions);
  }

  async sendRequestToServer(
    serviceCode,
    request,
    requestData,
    blockui,
    responseHandler,
    errorHandler
  ) {
    if (!blockui) {
      setTimeout(() => {
        console.log('Hide Loading...');
      });
    }

    try {
      const response = await this.sendPostToServer(
        `/${serviceCode}/${request}`,
        requestData
      );
      responseHandler(response.data); 
    } catch (error) {
      errorHandler(error); 
    }
  }
}

export default new RemoteService();