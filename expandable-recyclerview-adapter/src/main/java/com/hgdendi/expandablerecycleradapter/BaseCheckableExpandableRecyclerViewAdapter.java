/**
 * BaseCheckableExpandableRecyclerViewAdapter
 * https://github.com/hgDendi/ExpandableRecyclerView
 * <p>
 * Copyright (c) 2017 hg.dendi
 * <p>
 * MIT License
 * https://rem.mit-license.org/
 * <p>
 * email: hg.dendi@gmail.com
 * Date: 2017-10-18
 */
package com.hgdendi.expandablerecycleradapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class BaseCheckableExpandableRecyclerViewAdapter
        <GroupBean extends BaseCheckableExpandableRecyclerViewAdapter.CheckableGroupItem<ChildBean>,
                ChildBean,
                GroupViewHolder extends BaseCheckableExpandableRecyclerViewAdapter.BaseCheckableGroupViewHolder,
                ChildViewHolder extends BaseCheckableExpandableRecyclerViewAdapter.BaseCheckableChildViewHolder>
        extends BaseExpandableRecyclerViewAdapter<GroupBean, ChildBean, GroupViewHolder, ChildViewHolder> {

    private static final String TAG = BaseCheckableExpandableRecyclerViewAdapter.class.getSimpleName();

    private final Object PAYLOAD_CHECKMODE = this;
    public static final int CHECK_MODE_NONE = 0;
    public static final int CHECK_MODE_PARTIAL = CHECK_MODE_NONE + 1;
    public static final int CHECK_MODE_ALL = CHECK_MODE_NONE + 2;

    private final Set<CheckedItem<GroupBean, ChildBean>> mCheckedSet = new HashSet<>();
    private CheckStatusChangeListener<GroupBean, ChildBean> mOnCheckStatusChangeListener;

    /**
     * max num of items to be selected at the same time
     * if equals to 1 , new choice will override old choice
     * otherwise , the new checking-clickevent will be ignored
     */
    private int mMaxCheckedNum;

    public BaseCheckableExpandableRecyclerViewAdapter(int maxCheckedNum) {
        if (maxCheckedNum <= 0) {
            throw new IllegalArgumentException("invalid maxCheckedNum " + maxCheckedNum);
        }
        mMaxCheckedNum = maxCheckedNum;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                // after notifyDataSetChange(),clear outdated list
                mCheckedSet.clear();
            }
        });
    }

    public final Set<CheckedItem<GroupBean, ChildBean>> getCheckedSet() {
        return mCheckedSet;
    }

    public final int getSelectedCount() {
        return mCheckedSet.size();
    }

    public final void setOnCheckStatusChangeListener(CheckStatusChangeListener<GroupBean, ChildBean> onCheckStatusChangeListener) {
        mOnCheckStatusChangeListener = onCheckStatusChangeListener;
    }

    public final void setCheckedSet(List<CheckedItem<GroupBean, ChildBean>> checkedSet) {
        clearCheckedListAndUpdateUI();
        if (checkedSet == null || checkedSet.size() <= 0) {
            return;
        }
        for (CheckedItem<GroupBean, ChildBean> checkedItem : checkedSet) {
            addToCheckedList(checkedItem);
        }
    }

    @Override
    public void onBindGroupViewHolder(final GroupViewHolder groupViewHolder, final GroupBean groupBean, boolean isExpand) {
        if (groupViewHolder.getCheckableRegion() != null) {
            groupViewHolder.getCheckableRegion().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGroupChecked(
                            groupBean,
                            groupViewHolder,
                            translateToDoubleIndex(groupViewHolder.getAdapterPosition())[0]);
                }
            });
        }
    }

    @Override
    protected void onBindGroupViewHolder(GroupViewHolder groupViewHolder, GroupBean groupBean, boolean isExpand, List<Object> payload) {
        if (payload != null && payload.size() != 0) {
            if (payload.contains(PAYLOAD_CHECKMODE)) {
                groupViewHolder.setCheckMode(getGroupCheckedMode(groupBean));
            }
            return;
        }

        onBindGroupViewHolder(groupViewHolder, groupBean, isExpand);
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, final GroupBean groupBean, final ChildBean childBean) {
        holder.setCheckMode(getChildCheckedMode(childBean));
        if (holder.getCheckableRegion() != null) {
            holder.getCheckableRegion().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChildChecked(
                            holder,
                            groupBean,
                            childBean);
                }
            });
        }
    }

    @Override
    protected void onBindChildViewHolder(ChildViewHolder holder, GroupBean groupBean, ChildBean childBean, List<Object> payload) {
        if (payload != null && payload.size() != 0) {
            if (payload.contains(PAYLOAD_CHECKMODE)) {
                holder.setCheckMode(getChildCheckedMode(childBean));
            }
            return;
        }
        onBindChildViewHolder(holder, groupBean, childBean);
    }

    @Override
    protected void bindChildViewHolder(final ChildViewHolder holder, final GroupBean groupBean, final ChildBean childBean, List<Object> payload) {
        super.bindChildViewHolder(holder, groupBean, childBean, payload);
        holder.setCheckMode(getChildCheckedMode(childBean));
        if (holder.getCheckableRegion() != null) {
            holder.getCheckableRegion().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChildChecked(holder, groupBean, childBean);
                }
            });
        }
    }

    private int getGroupCheckedMode(GroupBean groupBean) {
        if (!groupBean.isExpandable()) {
            return isItemSelected(groupBean) ? CHECK_MODE_ALL : CHECK_MODE_NONE;
        } else {
            int checkedCount = 0;
            for (ChildBean childBean : groupBean.getChildren()) {
                if (isItemSelected(childBean)) {
                    checkedCount++;
                }
            }
            if (checkedCount == 0) {
                return CHECK_MODE_NONE;
            } else if (checkedCount == groupBean.getChildCount()) {
                return CHECK_MODE_ALL;
            } else {
                return CHECK_MODE_PARTIAL;
            }
        }
    }

    private int getChildCheckedMode(ChildBean childBean) {
        return isItemSelected(childBean) ? CHECK_MODE_ALL : CHECK_MODE_NONE;
    }

    private void onGroupChecked(GroupBean groupBean, GroupViewHolder holder, int groupIndex) {
        int checkedMode = getGroupCheckedMode(groupBean);
        if (groupBean.isExpandable()) {
            switch (checkedMode) {
                case CHECK_MODE_NONE:
                case CHECK_MODE_PARTIAL:
                    selectAllInGroup(holder, groupBean, groupIndex, true);
                    break;
                case CHECK_MODE_ALL:
                default:
                    selectAllInGroup(holder, groupBean, groupIndex, false);
                    break;
            }
        } else {
            if (isItemSelected(groupBean)) {
                if (!onInterceptGroupCheckStatusChanged(groupBean, false)
                        && removeFromCheckedList(groupBean)) {
                    holder.setCheckMode(getGroupCheckedMode(groupBean));
                }
            } else {
                if (!onInterceptGroupCheckStatusChanged(groupBean, true)
                        && addToCheckedList(groupBean)) {
                    holder.setCheckMode(getGroupCheckedMode(groupBean));
                }
            }
        }
    }

    private void selectAllInGroup(GroupViewHolder holder, GroupBean groupBean, int groupIndex, boolean selectAll) {
        if (selectAll && !isGroupExpanding(groupBean)) {
            expandGroup(groupBean);
        }
        final List<ChildBean> children = groupBean.getChildren();
        final int groupAdapterPosition = holder.getAdapterPosition();
        final int originalGroupCheckedMode = getGroupCheckedMode(groupBean);
        for (int i = 0; i < children.size(); i++) {
            ChildBean childBean = children.get(i);
            if (selectAll) {
                if (isItemSelected(childBean)) {
                    continue;
                }
                if (!onInterceptChildCheckStatusChanged(groupBean, childBean, true)) {
                    addToCheckedList(groupBean, childBean);
                    notifyItemChanged(groupAdapterPosition + i + 1, PAYLOAD_CHECKMODE);
                }
            } else {
                if (!isItemSelected(childBean)) {
                    continue;
                }
                if (!onInterceptChildCheckStatusChanged(groupBean, childBean, false)
                        && removeFromCheckedList(groupBean, childBean)) {
                    notifyItemChanged(groupAdapterPosition + i + 1, PAYLOAD_CHECKMODE);
                }
            }

        }
        final int currentGroupCheckedMode = getGroupCheckedMode(groupBean);
        if (currentGroupCheckedMode != originalGroupCheckedMode) {
            holder.setCheckMode(currentGroupCheckedMode);
        }
    }

    /**
     * lanmeng
     * 1. 制造一定的通胀与利润点能促进生产动力；
     * 2. 人对商品的需求程度是不一样的；不同商品的供给量、价格是不一样的。（商品划分：粮食、住房、医疗、教育、娱乐、兜底）
     * 3. 单次释放的闲置资金会在生产消费循环中在企业、居民、政府中分配；从中实现2大目的：经济建设、个人拥有值的绝对增量。基于此，逐步满足了对商品需求问题。
     * 4. 企业利润来源：居民存款与贷款、上下游和同行利润分配、政府政策。由于住房的高价格，导致了普通居民存款清空、背负负债。最终导致利润（资金）来源于1.生产力的提升、2.通胀带来的分配占比。
     * 5. 产业链的润滑性与成熟性问题，导致单次消化完冗余资金的速度越来越快。生产消费链上能分配的居民越来越少。
     * 6. 企业和政府贷款投入的再生产没有达到预期收益时，就会形成负债、破产等。 负债一方面短时间迅速提升生产力；另一方面埋下了巨大隐患。借贷如同跑2里地喘3口气，不借贷如同慢慢行走。只是有的人缓过来了，有的人累死了。整体看借贷提前创造生产力比不透支效果更好。
     * 7. 现在面临的问题：1.企业端传统各行充分发展、利润降低，员工需求减少。2.普通居民房贷问题闲置资金不足、贷款压力大。3.政府负债问题，及对外的竞争。
     * 8. 现代财政体系下，政府会不断地一方面倾向放水来释放流动性；另一方面欲通过新产业来带动需求与资金流动。
     * 9. 什么是一个好产业？ 对政府：1.强需求性（可用性）与大市场；2.就业岗位多；3.高技术性。 弊端：1.投资大；2.风险高、前景不明。
     * 10. A。促进生产的新能源、新材料；B。具备资源信息整合与加速能力的计算、通信行业；C。带来资金流的新应用领域（盯政策：如历史上的房产、新能源、低空经济，养老；投资建设等）；D。医药；E：偶尔的防范型领域：军事环保等。
     * 11. 通胀与社会福利；只要强需求型物质供给充足，管你其它物质通胀不通胀。未来的社会福利大概率是保障普通人的基础生存，想要进一步的生活就去干活。这样既能兜底，又可以引导资金不断流向新领域而带动发展，而避免对生活必需品的通胀。
     * 12. 同理别太迷信所谓的全球化分工。任何一个国家基于自身安全考虑，都要保障其粮食、产业链安全，除非它没能力。但当其处于贸易优势时，就会攻击对方贸易保护。
     * 13. 举债的实际目的：货币量化的稳定。在生产端创造生产意愿；在消费端创造提前享受物质。政府则强化了经济调控手段。
     * 而这本质是一种能引起人欲望的利润差。只要能制造欲望，就能带来生产意愿。
     * 14. 政府应尽量驱赶市场闲置资金和无息贷款来制造流动性。
     *
     *
     * @param holder
     * @param groupBean
     * @param childBean
     */
    private void onChildChecked(ChildViewHolder holder, GroupBean groupBean, ChildBean childBean) {
        final int originalGroupMode = getGroupCheckedMode(groupBean);
        boolean changeFlag = false;
        if (getChildCheckedMode(childBean) == CHECK_MODE_ALL) {
            if (!onInterceptChildCheckStatusChanged(groupBean, childBean, false)
                    && removeFromCheckedList(groupBean, childBean)) {
                holder.setCheckMode(getChildCheckedMode(childBean));
                changeFlag = true;
            }
        } else {
            if (!onInterceptChildCheckStatusChanged(groupBean, childBean, true)
                    && addToCheckedList(groupBean, childBean)) {
                holder.setCheckMode(getChildCheckedMode(childBean));
                changeFlag = true;
            }
        }

        if (changeFlag && getGroupCheckedMode(groupBean) != originalGroupMode) {
            notifyItemChanged(getAdapterPosition(getGroupIndex(groupBean)), PAYLOAD_CHECKMODE);
        }
    }

    private boolean onInterceptGroupCheckStatusChanged(GroupBean groupBean, boolean targetStatus) {
        return mOnCheckStatusChangeListener != null
                && mOnCheckStatusChangeListener.onInterceptGroupCheckStatusChange(groupBean, targetStatus);
    }

    private boolean onInterceptChildCheckStatusChanged(GroupBean groupBean, ChildBean childBean, boolean targetStatus) {
        return mOnCheckStatusChangeListener != null
                && mOnCheckStatusChangeListener.onInterceptChildCheckStatusChange(groupBean, childBean, targetStatus);
    }

    private boolean isItemSelected(GroupBean groupBean) {
        for (CheckedItem checkedItem : mCheckedSet) {
            if (checkedItem.getCheckedItem().equals(groupBean)) {
                return true;
            }
        }
        return false;
    }

    private boolean isItemSelected(ChildBean childBean) {
        for (CheckedItem checkedItem : mCheckedSet) {
            if (checkedItem.getCheckedItem().equals(childBean)) {
                return true;
            }
        }
        return false;
    }

    private boolean addToCheckedList(GroupBean groupBean) {
        return addToCheckedList(groupBean, null);
    }

    private boolean addToCheckedList(GroupBean groupBean, ChildBean childBean) {
        return addToCheckedList(new CheckedItem<>(groupBean, childBean));
    }

    private boolean addToCheckedList(CheckedItem<GroupBean, ChildBean> checkedItem) {
        if (mMaxCheckedNum == 1) {
            clearCheckedListAndUpdateUI();
        } else if (mMaxCheckedNum <= mCheckedSet.size()) {
            return false;
        }
        return mCheckedSet.add(checkedItem);
    }

    private void clearCheckedListAndUpdateUI() {
        Iterator<CheckedItem<GroupBean, ChildBean>> iter = mCheckedSet.iterator();
        while (iter.hasNext()) {
            final CheckedItem<GroupBean, ChildBean> checkedItem = iter.next();
            final int[] coord = getCoordFromCheckedItem(checkedItem);
            final GroupBean groupBean = getGroupItem(coord[0]);
            final int originalGroupCheckedStatus = getGroupCheckedMode(groupBean);
            iter.remove();
            final int groupAdapterPosition = getAdapterPosition(coord[0]);
            final int adapterPosition = groupAdapterPosition + coord[1] + 1;
            notifyItemChanged(adapterPosition, PAYLOAD_CHECKMODE);
            final int currentGroupCheckedStatus = getGroupCheckedMode(groupBean);
            if (coord[1] >= 0 && currentGroupCheckedStatus != originalGroupCheckedStatus) {
                notifyItemChanged(groupAdapterPosition, PAYLOAD_CHECKMODE);
            }
        }
    }

    private int[] getCoordFromCheckedItem(CheckedItem<GroupBean, ChildBean> checkedItem) {
        int[] result = new int[]{-1, -1};
        for (int i = 0; i < getGroupCount(); i++) {
            if (getGroupItem(i).equals(checkedItem.groupItem)) {
                result[0] = i;
                break;
            }
        }
        if (checkedItem.childItem != null) {
            result[1] = getGroupItem(result[0]).getChildren().indexOf(checkedItem.childItem);
        }
        return result;
    }

    private boolean removeFromCheckedList(GroupBean groupBean) {
        return removeFromCheckedList(groupBean, null);
    }

    private boolean removeFromCheckedList(GroupBean groupBean, ChildBean childBean) {
        return mCheckedSet.remove(new CheckedItem<>(groupBean, childBean));
    }

    public abstract static class BaseCheckableGroupViewHolder extends BaseGroupViewHolder implements Selectable {
        public BaseCheckableGroupViewHolder(View itemView) {
            super(itemView);
        }
    }

    public abstract static class BaseCheckableChildViewHolder extends RecyclerView.ViewHolder implements Selectable {
        public BaseCheckableChildViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface Selectable {
        /**
         * optimize for partial update
         * if an item is switching check mode ,
         * do not need to invalidate whole item,
         * this is the optimized callback
         *
         * @param mode
         */
        void setCheckMode(int mode);

        /**
         * checkable region
         * correspond to the check operation
         * <p>
         * ect.
         * the child item returns itself
         * the group item returns its check icon
         *
         * @return
         */
        View getCheckableRegion();
    }

    public interface CheckableGroupItem<ChildItem> extends BaseGroupBean<ChildItem> {
        /**
         * get children list
         *
         * @return
         */
        List<ChildItem> getChildren();
    }

    public static class CheckedItem<GroupItem, ChildItem> {
        GroupItem groupItem;
        ChildItem childItem;

        public CheckedItem(GroupItem groupItem, ChildItem childItem) {
            this.groupItem = groupItem;
            this.childItem = childItem;
        }

        public GroupItem getGroupItem() {
            return groupItem;
        }

        public ChildItem getChildItem() {
            return childItem;
        }

        Object getCheckedItem() {
            return childItem != null ? childItem : groupItem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CheckedItem that = (CheckedItem) o;

            if (!groupItem.equals(that.groupItem)) {
                return false;
            }
            return childItem != null ? childItem.equals(that.childItem) : that.childItem == null;

        }

        @Override
        public int hashCode() {
            int result = groupItem.hashCode();
            result = 31 * result + (childItem != null ? childItem.hashCode() : 0);
            return result;
        }
    }


    /**
     * Intercept of mode switch
     * <p>
     * returns true means intercept this mode switch
     *
     * @param <GroupItem>
     * @param <ChildItem>
     */
    public interface CheckStatusChangeListener<GroupItem extends BaseCheckableExpandableRecyclerViewAdapter.CheckableGroupItem<ChildItem>, ChildItem> {

        boolean onInterceptGroupCheckStatusChange(GroupItem groupItem, boolean targetStatus);

        boolean onInterceptChildCheckStatusChange(GroupItem groupItem, ChildItem childItem, boolean targetStatus);
    }
}