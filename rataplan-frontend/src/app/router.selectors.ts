import { ActivatedRouteSnapshot, Data, Params } from '@angular/router';
import { getRouterSelectors, RouterReducerState, SerializedRouterStateSnapshot } from '@ngrx/router-store';
import { createFeatureSelector, createSelector } from '@ngrx/store';

const selectRouter = createFeatureSelector<RouterReducerState>('router');

function* walkRoutes(state: SerializedRouterStateSnapshot): Generator<ActivatedRouteSnapshot> {
  for(let route: ActivatedRouteSnapshot | null = state.root; route; route = route.firstChild) {
    yield route;
  }
}

function* walkParams(state: SerializedRouterStateSnapshot): Generator<Params> {
  for(const route of walkRoutes(state)) {
    if(route.params) yield route.params;
  }
}

function* walkData(state: SerializedRouterStateSnapshot): Generator<Data> {
  for(const route of walkRoutes(state)) {
    if(route.data) yield route.data;
  }
}

const rawRouterSelectors = getRouterSelectors(selectRouter);

export const routerSelectors = {
  selectRouter,
  ...rawRouterSelectors,
  selectNestedParam: (param: string) => createSelector(selectRouter, ({state}) => {
    let ret: string | undefined = undefined;
    for(const p of walkParams(state)) {
      if(param in p) ret = p[param];
    }
    return ret;
  }),
  selectNestedParams: (param: string) => createSelector(selectRouter, ({state}) => {
    const ret: string[] = [];
    for(const p of walkParams(state)) {
      if(param in p) ret.push(p[param]);
    }
    return ret;
  }),
  selectHasNestedDataFlag: (flag: string) => createSelector(selectRouter, ({state}) => {
    for(const d of walkData(state)) {
      if(d[flag]) return true;
    }
    return false;
  }),
};