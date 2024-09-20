import React, { useEffect, useState } from 'react';
import { GoogleLogout } from "react-google-login";


function Logout() {
    const [credentials, setCredentials] = useState({ clientId: '', apiKey: '', scopes: '' });

    useEffect(() => {
        // Fetch credentials from the backend
        fetch('/api/credentials')
          .then(response => response.json())
          .then(data => setCredentials(data))
          .catch(error => console.error('Error fetching credentials:', error));
      }, []);

    const onSuccess = () => {
        console.log("logout Successful")
    }

    return(
        <div>
            <GoogleLogout
     clientId={credentials.clientId}
     buttonText={"logout"}
           onLogoutSuccess={onSuccess}
            />
        </div>
    )
}

export default Logout;