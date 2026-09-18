// 添加日期范围
export function addDateRange<T extends object>(
  params: T & { params?: Record<string, unknown> },
  dateRange: readonly string[] = [],
  propName = 'Time',
) {
  const search = params;

  search.params =
    typeof search.params === 'object' && search.params !== null && !Array.isArray(search.params)
      ? search.params
      : {};

  const range = Array.isArray(dateRange) ? dateRange : [];

  search.params[`begin${propName}`] = range[0];
  search.params[`end${propName}`] = range[1];

  return search;
}
