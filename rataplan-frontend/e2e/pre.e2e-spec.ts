import { browser } from 'protractor';

describe('Pre e2e', () => {
  let request = require('request');
  let servers = require('./servers.json');

  it('should ping servers', () => {
    let dbHelperSetup = false;
    let backendSetup = false;
    let authSetup = false;

    request(servers.dbHelperService, () => {
      dbHelperSetup = true;
    });

    request(servers.backendService, () => {
      backendSetup = true;
    });

    request(servers.authService, () => {
      authSetup = true;
    });

    browser.wait(function () {
      return backendSetup && dbHelperSetup && authSetup;
    }, 120000, 'Did not answer: ' +
    backendSetup ? '' : servers.backendService + ' ' +
    authSetup ? '' : servers.authService + ' ' +
    dbHelperSetup ? '' : servers.dbHelperService);

  });

});
