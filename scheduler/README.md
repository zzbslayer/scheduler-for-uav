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

