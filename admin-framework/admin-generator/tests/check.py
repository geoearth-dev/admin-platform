"""Compile the reactor, render generator fixtures and compile their Java output (no database needed)."""
from pathlib import Path
import os
import re
import shutil
import subprocess
import sys
import tempfile

root = Path(__file__).resolve().parents[3]
output = Path(tempfile.mkdtemp(prefix="admin-generator-check-"))
maven = shutil.which("mvn.cmd" if os.name == "nt" else "mvn")
if not maven:
    raise SystemExit("Maven is required")
with (output / "compile.log").open("w", encoding="utf-8") as log:
    result = subprocess.run([maven, "-o", "-pl", "admin-server", "-am", "-DskipTests", "-X", "compile"], cwd=root, stdout=log, stderr=subprocess.STDOUT)
if result.returncode:
    raise SystemExit(f"Reactor compile failed; see {output / 'compile.log'}")
text = (output / "compile.log").read_text(encoding="utf-8", errors="replace")
paths = re.findall(r"\(f\) compilePath = \[(.*?)\]", text)
if not paths:
    raise SystemExit(f"Compiler classpath was not found; see {output / 'compile.log'}")
classpath = [str(root / "admin-framework/admin-generator/src/main/resources"), *paths[-1].split(", ")]
classpath = os.pathsep.join(path.replace("\\", "/") for path in classpath)
source = Path(__file__).with_name("GeneratorSmoke.java")
quote = lambda value: '"' + str(value).replace("\\", "/") + '"'
(output / "javac.args").write_text(f"-encoding UTF-8 -cp {quote(classpath)} -d {quote(output)} {quote(source)}", encoding="utf-8")
subprocess.run(["javac", "@" + str(output / "javac.args")], cwd=root, check=True)
(output / "java.args").write_text(f"-cp {quote(str(output) + os.pathsep + classpath)} GeneratorSmoke {quote(output / 'generated')}", encoding="utf-8")
result = subprocess.run(["java", "@" + str(output / "java.args")], cwd=root)
print(f"Validation output: {output}")
sys.exit(result.returncode)
