# ========================================
# Windows MSI 安装程序构建脚本
# ========================================

# 设置 GraalVM 环境变量
$env:JAVA_HOME = "C:\Java\graalvm-ce-17"
$env:PATH = "C:\Java\graalvm-ce-17\bin;$env:PATH"

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host " 开始构建 MSI 安装程序" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# 验证环境
Write-Host "`n[检查] 验证构建环境..." -ForegroundColor Yellow
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "错误: 未找到 Maven，请确认已添加到 PATH" -ForegroundColor Red
    exit 1
}
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    Write-Host "错误: 未找到 jpackage，请检查 JDK 安装" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Maven: $(mvn -version | Select-Object -First 1)" -ForegroundColor Gray
Write-Host "✓ Java: $(java -version 2>&1 | Select-Object -First 1)" -ForegroundColor Gray

# 1. 清理旧构建
Write-Host "`n[1/4] 清理旧构建..." -ForegroundColor Yellow
Remove-Item target -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item installer -Recurse -Force -ErrorAction SilentlyContinue

# 2. 编译 Maven 项目
Write-Host "[2/4] 编译项目..." -ForegroundColor Yellow
mvn clean package -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "`n✗ Maven 编译失败" -ForegroundColor Red
    exit 1
}

# 验证 JAR 生成
if (-not (Test-Path "target\myappgui-1.0.0.jar")) {
    Write-Host "✗ JAR 文件未生成" -ForegroundColor Red
    exit 1
}
Write-Host "✓ JAR 文件已生成" -ForegroundColor Green

# 3. 清理临时文件
Write-Host "[3/4] 清理临时文件..." -ForegroundColor Yellow
Get-ChildItem target -Directory -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force

# 4. 生成 MSI
Write-Host "[4/4] 生成 MSI 安装程序（约 2-3 分钟）..." -ForegroundColor Yellow
jpackage `
    --type msi `
    --name GuessingNumberGame `
    --app-version 1.0.0 `
    --vendor Example `
    --input target `
    --main-jar myappgui-1.0.0.jar `
    --main-class com.example.myapp.GuessingNumberGUI `
    --dest installer `
    --win-dir-chooser `
    --win-menu `
    --win-shortcut

# 验证结果
if ($LASTEXITCODE -eq 0 -and (Test-Path "installer\*.msi")) {
    $msi = Get-Item "installer\*.msi"
    Write-Host "`n=====================================" -ForegroundColor Green
    Write-Host " 构建成功！" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host "MSI 文件: $($msi.Name)" -ForegroundColor Cyan
    Write-Host "大小: $([math]::Round($msi.Length/1MB,2)) MB" -ForegroundColor Cyan
    Write-Host "位置: $($msi.FullName)`n" -ForegroundColor Cyan
} else {
    Write-Host "`n✗ MSI 生成失败（退出码: $LASTEXITCODE）" -ForegroundColor Red
    exit 1
}
