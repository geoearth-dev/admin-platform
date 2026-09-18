/** CPU 使用率均为百分比，无需再乘以 100。 */
export interface ServerCpu {
  cpuNum: number
  total: number
  sys: number
  used: number
  wait: number
  free: number
}

/** 物理内存容量单位为 GB（1024 进制）。 */
export interface ServerMemory {
  total: number
  used: number
  free: number
  usage: number
}

/** JVM 堆内存容量单位为 MB（1024 进制）。 */
export interface ServerJvm {
  total: number
  max: number
  free: number
  used: number
  /** 已用堆内存占已分配堆内存的百分比。 */
  usage: number
  name: string
  version: string
  home: string
  startTime: string
  runTime: string
  inputArgs: string
}

export interface ServerSystem {
  computerName: string
  computerIp: string
  userDir: string
  osName: string
  osArch: string
}

export interface ServerDisk {
  dirName: string
  sysTypeName: string
  typeName: string
  /** 磁盘容量已包含单位，直接展示。 */
  total: string
  free: string
  used: string
  usage: number
}

export interface ServerMetrics {
  cpu: ServerCpu
  mem: ServerMemory
  jvm: ServerJvm
  sys: ServerSystem
  sysFiles: ServerDisk[]
}
