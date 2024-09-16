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

  async sendHtmlPostToServer(url, requestData) {
    const token = this.getCurrentToken();

    const httpOptions = {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        Authorization: token, 
      },
    };
    return await axios.get(`${this.BASE_URL}${url}`, requestData, httpOptions);
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

  async sendMultiPartRequestToTheServer(
    url,
    formData,
    responseHandler,
    errorHandler
  ) {
    const token = this.getCurrentToken();
    const httpOptions = {
      headers: {
        'Content-Type': 'multipart/form-data',
        Accept: 'application/json',
        Authorization: token,
      },
    };
  
    try {
      const response = await axios.post(`${this.BASE_URL}${url}`, formData, httpOptions);
      responseHandler(response.data);
    } catch (error) {
      errorHandler(error);
    }
  }
  
  async fetchDocument(url, responseHandler, errorHandler) {
    const token = this.getCurrentToken();
    const httpOptions = {
      headers: {
        'Authorization': token,
      },
      responseType: 'arraybuffer', // Important for binary data
    };

    try {
      const response = await axios.get(`${this.BASE_URL}/${url}`, httpOptions);
      responseHandler(response.data);
    } catch (error) {
      errorHandler(error);
    }
  }

async sendRequestToGetLatestDocumentId() {
  const token = this.getCurrentToken();
  return axios.get(`${this.BASE_URL}/documents/latest-id`, {
    headers: {
      Authorization: token, // Use the token from the class method
    },
  });
}

}

export default new RemoteService();