interface CssField {
  tag?: string
  children?: CssField[]
}

const styles: Partial<Record<string, string>> = {
  'el-rate': '.el-rate{display: inline-block; vertical-align: text-top;}',
  'el-upload': '.el-upload__tip{line-height: 1.2;}',
}

function addCss(cssList: string[], el: CssField) {
  const css = el.tag ? styles[el.tag] : undefined
  if (css && !cssList.includes(css)) {
    cssList.push(css)
  }
  if (el.children) {
    el.children.forEach((el2) => addCss(cssList, el2))
  }
}

export function makeUpCss(conf: { fields: CssField[] }) {
  const cssList: string[] = []
  conf.fields.forEach((el) => addCss(cssList, el))
  return cssList.join('\n')
}
