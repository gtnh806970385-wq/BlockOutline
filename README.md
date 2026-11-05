# BlockOutline

面向 Minecraft 1.7.10 / GTNH 的客户端方块轮廓渲染模组。它接管原版方块高亮事件，提供可配置的颜色、透明度、线宽、破坏动画和闪烁效果。

## 功能

- 自定义 RGBA 颜色、透明度和线宽
- 破坏动画：无、缩小、下降、淡出
- 可调节闪烁透明度与速度
- 与建筑之杖的额外位置轮廓兼容
- 手持 GT 扳手时保留 GT 机器原生九宫格轮廓
- 游戏内配置界面

## 安装

从 [GitHub Releases](https://github.com/gtnh806970385-wq/BlockOutline/releases/latest) 下载 `BlockOutline-1.0.0.jar`，放入客户端实例的 `mods` 目录。服务端无需安装。

## 配置

按 `Esc → Mod Options → BlockOutline` 打开配置界面，也可以编辑 `config/blockoutline.cfg`。

## 构建

需要 JDK 25。请在仓库根目录运行 Gradle Wrapper：

```bash
./gradlew build --no-daemon
```

Windows 命令提示符：

```bat
gradlew.bat build --no-daemon
```

构建产物位于 `build/libs/`。

## License

[MIT License](LICENSE)
