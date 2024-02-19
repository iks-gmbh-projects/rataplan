export enum EmailCycle {
  SUPPRESS,
  INSTANT,
  DAILY_DIGEST,
  WEEKLY_DIGEST,
}

export type EmailNotificationSettings = {
  defaultSettings: EmailCycle | undefined,
  categorySettings: Record<string, EmailCycle>,
  typeSettings: Record<string, EmailCycle>,
};

export type NetworkEmailNotificationSettings = {
  defaultSettings: keyof typeof EmailCycle | EmailCycle | undefined,
  categorySettings: Record<string, keyof typeof EmailCycle | EmailCycle>,
  typeSettings: Record<string, keyof typeof EmailCycle | EmailCycle>,
}