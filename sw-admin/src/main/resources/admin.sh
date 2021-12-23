#!/usr/bin/env bash
# 定义应用组名
group_name='admin'
# 定义应用名称
app_name='sw-admin'
# 定义应用版本
app_version='prd-1.0'
# 定义应用环境
profile_active='dev'
echo '----copy jar----'
# 1、停止旧容器
docker stop ${app_name}-${app_version}
echo '----stop container----'
# 2、删除旧容器
docker rm ${app_name}-${app_version}
echo '----rm container----'
# 3、删除旧镜像
docker rmi ${app_name}:${app_version}
echo '----rm image----'
# 4、打包编译docker镜像
docker build -t ${app_name}:${app_version} .
echo '----build image----'
# 5、执行新镜像
docker run -it --name ${app_name}-${app_version} -m 512m -d -p 8088:8088 -p 8443:8443 -v /home/sweb/${group_name}/logs:/Users/yuleilei/logs ${app_name}:${app_version}
echo '----start container----'