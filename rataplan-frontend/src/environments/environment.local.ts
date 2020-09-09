// for deployments on a local tomcat server.
const apiVersion: string = '/v1/';

export const environment = {
  production: true,
  heroku: false,
  backendUrl: 'http://localhost:8080' + apiVersion,
  baseUrl: 'http://localhost:4200'
};
