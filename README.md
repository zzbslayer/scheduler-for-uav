# Scheduler
根据节点时延、可用性等指标，按照一定策略调度服务。

# Auto-Scaling
根据访问量情况动态调整服务数量

# 附录
## reset k8s
```sh
kubeadm reset -f
kubeadm init --config=/home/reins/kubeadm.yml

kubeadm token create --print-join-command # print join command on master and execute it on slaves
kubectl proxy --address 0.0.0.0 --port=8080 --accept-hosts '.*'

```