# 使用方法
连接到 control plane 节点后执行以下命令
```sh
# compile scheduler
go build -o=_output/bin/kube-scheduler ./cmd/scheduler
# build image
docker build --no-cache . -t scheduler-framework-sample:test

# config setup
sudo cp manifests/custom-scheduler.yaml /etc/kubernetes/custom-scheduler.yaml
sudo cp manifests/kube-scheduler.yaml /etc/kubernetes/manifests/bak-custom-kube-scheduler.yaml
# backup for default config
sudo cp /etc/kubernetes/manifests/kube-scheduler.yaml /etc/kubernetes/manifests/bak-kube-scheduler.yaml
# after this line, k8s will automatically use our image as scheduler
sudo cp /etc/kubernetes/manifests/bak-custom-kube-scheduler.yaml /etc/kubernetes/manifests/kube-scheduler.yaml
# check
kubectl logs kube-scheduler-kube1 -n kube-system
kubectl get pods -l component=kube-scheduler -n kube-system -o=jsonpath="{.items[0].spec.containers[0].image}{'\n'}"

# each time we need to update the image, modify the kube-scheduler and recover it (k8s listens to the modification of /etc/kuberentes/manifests/kube-scheduler.yaml, so any modification to this file will cause the recreation of kube-scheduler image)
sudo cp /etc/kubernetes/manifests/bak-kube-scheduler.yaml /etc/kubernetes/manifests/kube-scheduler.yaml
sudo cp /etc/kubernetes/manifests/bak-custom-kube-scheduler.yaml /etc/kubernetes/manifests/kube-scheduler.yaml

```

# 理论方法
无人机集群中有一个 Cluster Head(CH) 负责集群对外通信。

无人机网络中的节点 i 可用性 `a^i_{node}` 需要结合节点自身可用性 `a^i_{self}` 以及其他节点可用性 `a^i_{other}` 一起考虑。

```latex
// temp solution
a^i_{self} = f(a^i_{machine}, bp_i, **kwargs)
```

计算 `a^i_{self}` 时，可以结合目前已有的可用性模型。目前考虑以下因素，节点 i 自身机器可用性是 `a^i_{machine}`，剩余电量百分比 `bp_i`。


计算 `a^i_{other}` 时要结合网络拓扑结构考虑，其他节点挂了导致该节点与 CH 不连通也就意味着该节点挂了。因此可以用当前节点与 CH 之间的割断开的概率来表示 `a^i_{other}`，最小割断开的概率是最大的，因此可以用最小割断开的概率估计 `a^i_{other}`。


```latex
a^i_{node} = g(a^i_{self}, a^i_{other})
```


最小割估计的方式与图卷积的思想类似。


# 附录
## 更新依赖
```sh
# update dependencies
go mod download
go mod tidy
go mod vendor
```

## 编译、打包镜像
```sh
CGO_ENABLED=0 GOOS=linux GOARCH=amd64
go build -o=_output/bin/kube-scheduler ./cmd/scheduler
# build image
docker build --no-cache . -t scheduler-framework-sample:test
```

## 测试
```sh
# test locally
./_output/bin/kube-scheduler --config=./_deploy/scheduler-config.yaml --v=3
# test in docker
docker run -itd scheduler-framework-sample:test kube-scheduler --config=/scheduler-config.yaml
docker run -it scheduler-framework-sample:test bash

sudo cp ~/wjs/scheduler-for-uav/scheduler-framework-sample/_deploy/custom-scheduler.yaml /etc/kubernetes/custom-scheduler.yaml
sudo cp ~/wjs/scheduler-for-uav/scheduler-framework-sample/_deploy/kube-scheduler.yaml /etc/kubernetes/manifests/bak-custom-kube-scheduler.yaml

sudo cp bak-custom-kube-scheduler.yaml kube-scheduler.yaml
sudo cp bak-kube-scheduler.yaml kube-scheduler.yaml
# test in k8s
kubectl get pods -n kube-system
kubectl logs kube-scheduler-kube1 -n kube-system
kubectl get pods -l component=kube-scheduler -n kube-system -o=jsonpath="{.items[0].spec.containers[0].image}{'\n'}"

kubectl apply -f test-scheduler.yaml
```

