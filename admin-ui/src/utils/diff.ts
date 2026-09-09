// 比较两个数组是否相等（忽略顺序）
function arraysEqual<T>(a: T[], b: T[]): boolean {
  if (a.length !== b.length) return false;
  const counter = new Map<T, number>();
  for (const value of a) {
    counter.set(value, (counter.get(value) || 0) + 1);
  }
  for (const value of b) {
    const count = counter.get(value);
    if (count === undefined || count === 0) {
      return false;
    }
    counter.set(value, count - 1);
  }
  return true;
}

// 比较两个数组是否相等（顺序敏感）
function arraysStrictEqual<T>(a: T[], b: T[]): boolean {
  return a.length === b.length && a.every((value, index) => value === b[index]);
}

type DiffResult<T> = Partial<{ [K in keyof T]: T[K] extends object ? DiffResult<T[K]> : T[K]; }>;

type ArrayComparator = (a: unknown[], b: unknown[]) => boolean;
function createDiff(arrayEquals: ArrayComparator) {
  return function <T extends Record<string, unknown>>(obj1: T, obj2: T): DiffResult<T> {
    function findDifferences(o1: unknown, o2: unknown): unknown {
      if (Array.isArray(o1) && Array.isArray(o2)) {
        return arrayEquals(o1, o2) ? undefined : o2
      }

      if (typeof o1 === 'object' && typeof o2 === 'object' && o1 !== null && o2 !== null) {
        const diffResult: Record<string, unknown> = {}
        const keys = new Set([...Object.keys(o1), ...Object.keys(o2)]);
        for (const key of keys) {
          const valueDiff = findDifferences((o1 as Record<string, unknown>)[key], (o2 as Record<string, unknown>)[key]);
          if (valueDiff !== undefined) {
            diffResult[key] = valueDiff;
          }
        };

        return Object.keys(diffResult).length > 0 ? diffResult : undefined;
      }

      return o1 === o2 ? undefined : o2;
    }

    return findDifferences(obj1, obj2) as DiffResult<T>;
  };
}

// 数组比较（不含顺序）
const diff = createDiff(arraysEqual);

// 数组比较（含顺序）
const diffStrict = createDiff(arraysStrictEqual);

export { arraysEqual, arraysStrictEqual, diff, diffStrict };
