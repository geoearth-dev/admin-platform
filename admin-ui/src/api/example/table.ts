/** 仅供组件示例使用的本地数据，不发起后端请求。 */
export interface ExampleTableRow {
  id: string
  name: string
  category: string
  color: string
  productName: string
  price: string
  releaseDate: string
  imageUrl: string
  imageUrl2: string
  open: boolean
  status: 'error' | 'success' | 'warning'
}

export interface ExampleTableQuery {
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: string | null
  category?: string
  color?: string
  productName?: string
  price?: string
  start?: string
  end?: string
}

function sampleImage(index: number) {
  const colors = ['#409eff', '#67c23a', '#e6a23c']
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="160" height="120"><rect width="160" height="120" rx="12" fill="${colors[index % colors.length]}"/><text x="80" y="70" text-anchor="middle" fill="white" font-size="24">Item ${index + 1}</text></svg>`
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
}

const rows: ExampleTableRow[] = Array.from({ length: 120 }, (_, index) => {
  const date = new Date()
  date.setDate(date.getDate() - (index % 30))
  date.setHours(10, 0, 0, 0)
  const productName = `示例商品 ${String(index + 1).padStart(3, '0')}`
  const status = (['success', 'warning', 'error'] as const)[index % 3]!
  return {
    id: String(index + 1),
    name: productName,
    category: `Category${(index % 4) + 1}`,
    color: `Color${(index % 3) + 1}`,
    productName,
    price: (29 + index * 7.5).toFixed(2),
    releaseDate: date.toISOString(),
    imageUrl: sampleImage(index),
    imageUrl2: sampleImage(index + 1),
    open: index % 2 === 0,
    status,
  }
})

export async function getExampleTableApi(query: ExampleTableQuery = {}) {
  const fields = ['category', 'color', 'productName', 'price'] as const
  const start = query.start
    ? new Date(`${query.start}T00:00:00`).getTime()
    : -Infinity
  const end = query.end
    ? new Date(`${query.end}T23:59:59.999`).getTime()
    : Infinity
  let result = rows.filter((row) => {
    const time = Date.parse(row.releaseDate)
    return (
      time >= start &&
      time <= end &&
      fields.every((field) => {
        const keyword = query[field]?.trim().toLowerCase()
        return !keyword || row[field].toLowerCase().includes(keyword)
      })
    )
  })

  const sortBy = query.sortBy
  if (sortBy && rows[0] && Object.hasOwn(rows[0], sortBy) && query.sortOrder) {
    const field = sortBy as keyof ExampleTableRow
    const direction = query.sortOrder === 'desc' ? -1 : 1
    result = result.sort(
      (a, b) =>
        direction *
        String(a[field]).localeCompare(String(b[field]), 'zh-CN', {
          numeric: true,
        }),
    )
  }

  const page = Math.max(1, query.page ?? 1)
  const pageSize = Math.max(1, query.pageSize ?? 20)
  // 每次返回副本，单元格编辑不会污染其他示例和下一次查询。
  return {
    items: result
      .slice((page - 1) * pageSize, page * pageSize)
      .map((row) => ({ ...row })),
    total: result.length,
  }
}
