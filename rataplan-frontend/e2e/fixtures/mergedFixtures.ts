import { mergeTests } from '@playwright/test';
import { navigationTest } from './navigation/navigation.fixture';
import { umfragenTest } from './umfragen/umfragen.fixture';

export const test = mergeTests(navigationTest, umfragenTest);
