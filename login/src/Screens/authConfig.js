import { PublicClientApplication } from '@azure/msal-browser';

const msalConfig = {
  auth: {
    clientId: 'ae8ad555-89cf-4bc8-888a-f89be857f802', // Your application's client ID
    authority: 'https://login.microsoftonline.com/d2a0406e-e816-4f96-a2ce-ae9ed02a7733', // Your tenant ID
    redirectUri: 'http://localhost:3000', // Redirect URI after authentication
  },
};

const pca = new PublicClientApplication(msalConfig);

export default pca;
