// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `angular-cli.json`.
const apiVersion = '/v1';

export const environment = {
  production: false,
  heroku: false,
  // backendUrl: 'http://localhost:8080/' + apiVersion,
  backendUrl: 'http://localhost:8080' + apiVersion,
  baseUrl: 'http://localhost:4200'
};
