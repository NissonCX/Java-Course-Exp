#!/bin/bash

# 停止 Nacos 服务器
echo "🛑 停止 Nacos 服务器..."

cd ~/nacos/bin
sh shutdown.sh

echo "✅ Nacos 已停止"
