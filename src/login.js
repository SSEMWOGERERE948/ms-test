import React, { useEffect, useState } from "react";
import { GoogleLogin } from "react-google-login";

function Login() {
  const [credentials, setCredentials] = useState({
    clientId: "",
    apiKey: "",
    scopes: "",
  });

  useEffect(() => {
    // Fetch credentials from the backend
    fetch("/api/credentials")
      .then((response) => response.json())
      .then((data) => setCredentials(data))
      .catch((error) => console.error("Error fetching credentials:", error));
  }, []);

  const onSuccess = (res) => {
    console.log("login success. current user: ", res.profileObj);
  };

  const onFailure = (res) => {
    console.log("login failed. response: ", res);
  };
  return (
    <div id="signInbutton">
      <GoogleLogin
        clientId={credentials.clientId}
        buttonText="Login"
        onSuccess={onSuccess}
        onFailure={onFailure}
        cookiePolicy={"single_host_origin"}
        isSignedIn={true}
      />
    </div>
  );
}

export default Login;
