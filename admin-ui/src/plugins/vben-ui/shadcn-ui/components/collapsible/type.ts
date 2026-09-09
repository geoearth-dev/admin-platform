export interface CollapsibleParamsProps {
  defaultOpen?: boolean;
  maxHeight?: number | string;
  params: CollapsibleParamSchema[];
  visibleCount?: number;
}

export interface CollapsibleParamOption {
  [key: string]: unknown;
  max?: number;
  min?: number;
  precision?: number;
  step?: number;
  type?: 'exponential' | 'number' | 'select' | 'string';
}

export interface CollapsibleParamSchema {
  defaultValue?: number | number[] | string | string[];
  description: string;
  key: string;
  option: CollapsibleParamOption;
}
