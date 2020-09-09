// for deployments on the IKS linux test server
// has no effect for Heroku Test deployments

const apiVersion = 'v1/';
// const testRataplanUrl = 'http://192.168.5.31:8080/';
const testRataplanUrl = 'https://rataplan-backend-test.herokuapp.com/';

// baseUrl of application (in browser)
// const baseUrl = "http://192.168.5.31:9710/rataplan/";
const baseUrl = 'https://rataplan-frontend-test.herokuapp.com/';

export const environment = {
  production: true,
  heroku: false,
  backendUrl: testRataplanUrl + apiVersion,
  baseUrl: baseUrl
};
