# BlockOutline

Minecraft 1.7.10 GTNH 方块轮廓渲染优化模组。

完全接管 `DrawBlockHighlightEvent`，提供自定义颜色、透明度、粗细的方块选择轮廓，支持破坏动画和闪烁效果。

## 功能
- ✅ **自定义轮廓**：RGBA 颜色、透明度、粗细可配置
- ✅ **破坏动画**：无/缩小/下降/透明 四种模式
- ✅ **闪烁效果**：可调节闪烁透明度和速度
- ✅ **建筑之杖兼容**：手持 BetterBuildersWands 时额外位置轮廓正常显示
- ✅ **GT 机器九宫格**：手持扳手看 GT 机器时彩色轮廓 + 原生九宫格共存
- ✅ **配置 GUI**：游戏内可视化调节所有参数

## 技术栈
- Java 25 → source/target 1.8
- RetroFuturaGradle 2.0.2
- Forge DrawBlockHighlightEvent

## 构建
```batch
set JAVA_HOME=D:\Prism\java\java-runtime-epsilon
cd BlockOutline-1.0.0
gradlew.bat build --no-daemon
```

## 配置
游戏内按 ESC → Mod Options → BlockOutline 打开配置界面，或手动编辑 `config/blockoutline.cfg`。

## 许可证
MIT License