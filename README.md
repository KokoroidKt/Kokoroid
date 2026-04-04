# Kokoroid

[中文](README.md)

> [!IMPORTANT]
>
> Kokoroid还处于0.x版本，API尚未稳定，大部分API可能在未来有更改！
>
> Kokoroid is currently in version 0.x.
> Please note that the API is unstable and is subject to change in future releases!

## 简介

Kokoroid是一个跨平台，插件化的kotlin聊天机器人框架，
Kokoroid完全基于Kotlinx.coroutines，并提供简单明确的插件开发体验

Kokoroid目前还处于0.x版本，还有大部分内容等待实现

你可以在[这里](https://kokoroidkt.dev/)阅读使用文档

## 特点

- 易于使用：Kokoroid的核心目标是**降低机器人用户/开发者**的心智负担，在全流程尽可能简化部署/开发
- 灵活拓展：Kokoroid拓展易于插拔，且职责分离，维护Kokoroid实例更加简单
- 事件/动作：所有的消息（例如OnebotV11概念下的消息/通知/请求）都被整合为事件，而所有Bot操作则被整合为Bot实例的动作方法

## 计划

若以下所有功能完整实现，Kokoroid将发布1.0版本

- [x] 核心框架
- [ ] 更丰富的拓展API
- [ ] 开发文档/使用文档
- [ ] Kokoroid Cli
- [ ] Kokoroid拓展包管理器
- [ ] 官方正向Websocket Driver
- [ ] 官方反向Websocket Driver
- [ ] 官方HTTP Server Driver
- [ ] 官方HTTP Client Driver
- [ ] 对[Milky](https://milky.ntqqrev.org/)和[Onebot V11](https://github.com/botuniverse/onebot-11)的适配器支持

## 许可证

Kokoroid使用GNU Lesser General Public
License-2.1许可证进行开源，了解更多请访问：[Wikipedia - GNU Lesser General Public License](https://en.wikipedia.org/wiki/GNU_Lesser_General_Public_License)
