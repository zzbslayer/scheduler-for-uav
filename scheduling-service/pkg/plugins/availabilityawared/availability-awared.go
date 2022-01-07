package availabilityawared

import (
	"context"
	"fmt"
	"math"

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/klog/v2"
	"k8s.io/kubernetes/pkg/scheduler/framework"
)

const (
	// Name is plugin name
	Name = "availabilityawared"
)

var _ framework.FilterPlugin = &AvailabilityAwared{}
var _ framework.PreBindPlugin = &AvailabilityAwared{}
var _ framework.ScorePlugin = &AvailabilityAwared{}

type AvailabilityAwared struct {
	handle framework.Handle
}

func New(_ runtime.Object, handle framework.Handle) (framework.Plugin, error) {
	return &AvailabilityAwared{
		handle: handle,
	}, nil
}

func (aa *AvailabilityAwared) Name() string {
	return Name
}

func (aa *AvailabilityAwared) Filter(ctx context.Context, state *framework.CycleState, pod *v1.Pod, node *framework.NodeInfo) *framework.Status {
	klog.Infof("[Availability-Awared] filter pod: %v", pod.Name)
	return framework.NewStatus(framework.Success, "")
}

func (aa *AvailabilityAwared) Score(ctx context.Context, state *framework.CycleState, pod *v1.Pod, nodeName string) (int64, *framework.Status) {
	klog.Infof("[Latency-Awared] score pod: %v", pod.Name)
	nodeInfo, err := aa.handle.SnapshotSharedLister().NodeInfos().Get(nodeName)
	if err != nil {
		return 0, framework.NewStatus(framework.Error, fmt.Sprintf("getting node %q from Snapshot: %v", nodeName, err))
	}

	return aa.score(nodeInfo)
}

func (aa *AvailabilityAwared) score(_ *framework.NodeInfo) (int64, *framework.Status) {
	return 1, nil
}

func (aa *AvailabilityAwared) ScoreExtensions() framework.ScoreExtensions {
	return aa
}

func (aa *AvailabilityAwared) NormalizeScore(ctx context.Context, state *framework.CycleState, pod *v1.Pod, scores framework.NodeScoreList) *framework.Status {
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

func (aa *AvailabilityAwared) PreBind(ctx context.Context, state *framework.CycleState, pod *v1.Pod, nodeName string) *framework.Status {
	_, err := aa.handle.SnapshotSharedLister().NodeInfos().Get(nodeName)
	if err != nil {
		return framework.NewStatus(framework.Error, err.Error())
	}
	klog.Info("[Availability-Awared] prebind node")
	//klog.Infof("[Availability-Awared] prebind node info: %+v", nodeInfo.Node())
	return framework.NewStatus(framework.Success, "")
}
