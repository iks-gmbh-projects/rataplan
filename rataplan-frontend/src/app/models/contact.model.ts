export type ContactGroup = {
  id: string|number,
  name: string,
  contacts: (string|number)[],
};

export type AllContacts = {
  groups: ContactGroup[],
  ungrouped: (string|number)[],
};