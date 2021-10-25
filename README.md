# reset k8s
```sh
kubeadm reset -f
kubeadm init --config=/home/reins/kubeadm.yml

kubeadm token create --print-join-command # print join command on master and execute it on slaves
```