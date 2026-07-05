import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, resolve } from 'node:path'

const scriptDir = dirname(fileURLToPath(import.meta.url))
const viewPath = resolve(scriptDir, '../src/views/admin/StudentManagementView.vue')
const source = readFileSync(viewPath, 'utf8')

const expectedInputId = 'student-csv-file-input'

if (!source.includes(`id="${expectedInputId}"`)) {
  throw new Error(`CSV import view must define native file input #${expectedInputId}`)
}

const hasNativeFileInput = new RegExp(
  `<input[\\s\\S]*id="${expectedInputId}"[\\s\\S]*type="file"[\\s\\S]*accept=".csv,text/csv"`,
  'm'
).test(source)

if (!hasNativeFileInput) {
  throw new Error('CSV import view must expose a native CSV file input')
}

const hasBoundSelectControl = new RegExp(
  `<label[\\s\\S]*for="${expectedInputId}"[\\s\\S]*>\\s*选择CSV文件\\s*</label>`,
  'm'
).test(source)

if (!hasBoundSelectControl) {
  throw new Error('CSV select control must be a label bound to the native file input')
}

const hasSelectedFileAlert =
  source.includes('v-if="selectedFile"') &&
  source.includes('class="selected-file-alert"') &&
  source.includes(':title="`已添加文件：${selectedFile.name}`"')

if (!hasSelectedFileAlert) {
  throw new Error('CSV import dialog must show a visible added-file alert after selecting a file')
}
