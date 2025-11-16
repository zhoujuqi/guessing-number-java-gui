# ========================================
# Windows MSI 安装程序构建脚本
# ========================================

Write-Host "开始生成 MSI 安装程序..." -ForegroundColor Cyan

# 设置 JRE 路径
$env:JAVA_HOME = "C:\Java\graalvm-ce-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 生成 MSI
jpackage `
    --type msi `
    --name GuessingNumberGame `
    --app-version 1.0.0 `
    --vendor Example `
    --input target `
    --main-jar myappgui-1.0.0.jar `
    --main-class com.example.myapp.GuessingNumberGUI `
    --runtime-image $env:JAVA_HOME `
    --dest installer `
    --win-dir-chooser `
    --win-menu `
    --win-shortcut

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ MSI 生成成功: installer\GuessingNumberGame-1.0.0.msi" -ForegroundColor Green
} else {
    Write-Host "✗ MSI 生成失败" -ForegroundColor Red
    exit 1
}
