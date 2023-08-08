# plugin-template

Halo 2.0 插件开发快速开始模板。

## 开发环境

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/hello-world>

```bash
git clone git@github.com:liuyiwuqing/halo-plugin-template.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/halo-plugin-template.git
```

```bash
cd path/to/halo-plugin-template
```

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/halo-plugin-template"
```
