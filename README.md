# ClickRangeChangeTextView
可以扩大或缩小点击热区的控件,修复了setTouchDelegate使用上的一些坑




//        private void statItemAdUseTwoLineStyle(Context context,
//                                               RecommendAlbumInModuleAdapter adapter,
//                                               int firstItemPosition, int lastItemPosition) {
//            if (adapter == null || lastItemPosition <= firstItemPosition) {
//               return;
//            }
//
//            // 每行展示的数量
//            int lineShowedNum = (lastItemPosition - firstItemPosition + 1) / 2;
//
//            // 第一行首个展示的位置
//            int firstLineFirstPosition = (firstItemPosition - 1) / 2;
//            // 第一行最后展示的位置
//            int firstLineLastPosition = firstLineFirstPosition + lineShowedNum - 1;
//
//            // 第二行首个展示的位置
//            int secondLineFirstPosition = firstLineFirstPosition + adapter.getItemCount() / 2 - 1;
//            // 第二行最后展示的位置
//            int secondLineLastPostion = secondLineFirstPosition + lineShowedNum - 1;
//
//            realShowAd(context, adapter, secondLineFirstPosition, secondLineLastPostion);
//
//            realShowAd(context, adapter, firstLineFirstPosition, firstLineLastPosition);
//
//            resetAdStatus(adapter, 0, firstLineFirstPosition);
//
//            resetAdStatus(adapter, firstLineLastPosition + 1, secondLineFirstPosition);
//
//            resetAdStatus(adapter, secondLineLastPostion + 1, adapter.getItemCount());
//        }
