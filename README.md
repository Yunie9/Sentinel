# 简介
本项目为 Sentinel Dashboard 与 Apollo Config 的集成，fork 自 [Alibaba Sentinel](https://github.com/alibaba/Sentinel.git) 项目。

主要为 Sentinel Dashboard 集成 Apollo Config，使用 push 模式实现客户端配置的动态更新。

>生产环境下一般更常用的是 push 模式的数据源。对于 push 模式的数据源,如
> 远程配置中心（ZooKeeper, Nacos, Apollo等等），推送的操作不应由 
> Sentinel 客户端进行，而应该经控制台统一进行管理，直接进行推送，数据源
> 仅负责获取配置中心推送的配置并更新到本地。因此推送规则正确做法应该是 
> 配置中心控制台/Sentinel 控制台 → 配置中心 → Sentinel 数据源 → 
> Sentinel，而不是经 Sentinel 数据源推送至配置中心。
> 
> —— [Sentinel 官方文档](https://github.com/alibaba/Sentinel/wiki/%E5%9C%A8%E7%94%9F%E4%BA%A7%E7%8E%AF%E5%A2%83%E4%B8%AD%E4%BD%BF%E7%94%A8-Sentinel)

