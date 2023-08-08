# halo-plugin-nps

Halo 2.0 的Nps管理插件, 支持在 Console 进行注册登录Nps。

## 使用方式

1. 在 [Releases](https://github.com/liuyiwuqing/halo-plugin-nps/releases) 下载最新的 JAR 文件。
2. 在 Halo 后台的插件管理上传 JAR 文件进行安装。

> 需要注意的是，此插件需要配合lywq-nps服务端才能正常使用。

```bash
docker pull liuyiwuqing/lywq-nps:1.0.0
```

## 开发环境

```bash
git clone git@github.com:liuyiwuqing/halo-plugin-nps.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/halo-plugin-nps.git
```

```bash
cd path/to/halo-plugin-nps
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
    classes-directories:
      - "build/classes"
      - "build/resources"
    lib-directories:
      - "libs"
    fixedPluginPath:
      - "/path/to/halo-plugin-nps"
```
