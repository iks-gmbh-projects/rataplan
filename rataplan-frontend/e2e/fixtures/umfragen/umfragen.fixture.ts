import { test as base } from '@playwright/test';
import { Umfragen } from './umfragen';

type UmfragenFixture = {
  umfragen: Umfragen;
}

export const umfragenTest = base.extend<UmfragenFixture>({
  umfragen: async({page}, use) => {
    await use(new Umfragen(page));
  },
});
