# 运行须知

Kubernetes 提供 Scheduler Framework 接口可以实现自定义的调度逻辑。

需要停止原本的 kube-scheduler 才能使用。

## Plugin 与 Scheduler 的关系

以本项目中的 `pkg/plugins/sample/sample.go` 为例，我们实现了一个名为 `sample` 的插件，这个插件实现了 `Filter` 与 `Prebind` 两个扩展点。

项目运行时，将 `sample` 插件注册到 Kubernetes 集群中，并根据 `_deploy/scheduler-config.yaml` 中的配置，创建了一个名为 `scheduler-framework-sample` 的调度器实例，这个调度器的 `Filter` 与 `Prebind` 启用了来自 `sample` 插件的实现。
 
## 运行

修改 control plane 节点上的 /etc/kuberentes/manifest/kube-scheduler.yaml 将其中的 image 修改为我们的镜像，其他参数根据需要求改。

# 代码脚本
## reset k8s
```sh
kubeadm reset -f
kubeadm init --config=/home/reins/kubeadm.yml

kubeadm token create --print-join-command # print join command on master and execute it on slaves
```

## build & test
```sh
# update dependencies
go mod download
go mod tidy
go mod vendor

CGO_ENABLED=0 GOOS=linux GOARCH=amd64
go build -o=_output/bin/scheduler-framework-sample ./cmd/scheduler
# build image
docker build --no-cache . -t scheduler-framework-sample:$(TAG)
docker build --no-cache . -t scheduler-framework-sample:test
# test locally
./_output/bin/scheduler-framework-sample --config=./_deploy/scheduler-config.yaml --v=3
# test in docker
docker run -itd scheduler-framework-sample:test scheduler-framework-sample --config=/scheduler-config.yaml --kubeconfig=/kube-config.yaml
docker run -itd scheduler-framework-sample:test
# test in k8s
kubectl apply -f ./deploy
```

## publish image
```sh
# export
docker save $(IMAGE_ID) > scheduler-framework-sample.tar
docker save b9eaed08cb9a > scheduler-framework-sample.tar
scp scheduler-framework-sample.tar reins@10.0.0.217:/home/reins
# import
docker load < scheduler-framework-sample.tar
# rename
docker tag $(IMAGE_ID) scheduler-framework-sample:test
docker tag b9eaed08cb9a scheduler-framework-sample:test

```

