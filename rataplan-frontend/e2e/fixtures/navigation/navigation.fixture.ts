import { test as base } from '@playwright/test';
import { NavigationToAbstimmung } from './navigationToAbstimmung';
import { NavigationToFeedback } from './navigationToFeedback';
import { NavigationToUmfragen } from './navigationToUmfragen';

type NavigationFixture = {
  navigationToAbstimmung: NavigationToAbstimmung;
  navigationToFeedback: NavigationToFeedback;
  navigationToUmfragen: NavigationToUmfragen;
}

export const navigationTest = base.extend<NavigationFixture>({});