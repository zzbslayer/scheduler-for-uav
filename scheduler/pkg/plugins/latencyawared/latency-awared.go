package latencyawared

import (
	"context"
	"math"

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/klog/v2"
	"k8s.io/kubernetes/pkg/scheduler/framework"
)

const (
	// Name is plugin name
	Name = "latencyawared"
)

var _ framework.FilterPlugin = &LatencyAwared{}
var _ framework.PreBindPlugin = &LatencyAwared{}
var _ framework.ScorePlugin = &LatencyAwared{}


type LatencyAwared struct {
	handle framework.Handle
}

func New(_ runtime.Object, handle framework.Handle) (framework.Plugin, error) {
	return &LatencyAwared{
		handle: handle,
	}, nil
}

func (la *LatencyAwared) Name() string {
	return Name
}

func (la *LatencyAwared) Filter(ctx context.Context, state *framework.CycleState, pod *v1.Pod, node *framework.NodeInfo) *framework.Status {
	klog.Infof("[Latency-Awared] filter pod: %v", pod.Name)
	return framework.NewStatus(framework.Success, "")
}

func (la *LatencyAwared) Score(ctx context.Context, state *framework.CycleState, pod *v1.Pod, nodeName string) (int64, *framework.Status) {
	var score int64 = 1
	klog.Infof("[Latency-Awared] score pod %v for %v", pod.Name, score)
	return score, nil
}

func (la *LatencyAwared) ScoreExtensions() framework.ScoreExtensions {
	return la
}

func (la *LatencyAwared) NormalizeScore(ctx context.Context, state *framework.CycleState, pod *v1.Pod, scores framework.NodeScoreList) *framework.Status {
	// Find highest and lowest scores.
	var highest int64 = -math.MaxInt64
	var lowest int64 = math.MaxInt64
	for _, nodeScore := range scores {
		if nodeScore.Score > highest {
			highest = nodeScore.Score
		}
		if nodeScore.Score < lowest {
			lowest = nodeScore.Score
		}
	}

	// Transform the highest to lowest score range to fit the framework's min to max node score range.
	oldRange := highest - lowest
	newRange := framework.MaxNodeScore - framework.MinNodeScore
	for i, nodeScore := range scores {
		if oldRange == 0 {
			scores[i].Score = framework.MinNodeScore
		} else {
			scores[i].Score = ((nodeScore.Score - lowest) * newRange / oldRange) + framework.MinNodeScore
		}
	}

	return nil
}

func (la *LatencyAwared) PreBind(ctx context.Context, state *framework.CycleState, pod *v1.Pod, nodeName string) *framework.Status {
	_, err := la.handle.SnapshotSharedLister().NodeInfos().Get(nodeName)
	if err != nil {
		return framework.NewStatus(framework.Error, err.Error())
	}
	klog.Info("[Latency-Awared] prebind")
	//klog.Infof("[Latency-Awared] prebind node info: %+v", nodeInfo.Node())
	return framework.NewStatus(framework.Success, "")
}
