export interface CrontabValue {
  second: string;
  min: string;
  hour: string;
  day: string;
  month: string;
  week: string;
  year: string;
}
export type CrontabField = keyof CrontabValue;
export type DayRule = '' | 'lastDay' | 'workDay' | 'weekDay' | 'assWeek' | 'lastWeek';
export type DateArrays = [number[], number[], number[], number[], number[], number[]];
