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