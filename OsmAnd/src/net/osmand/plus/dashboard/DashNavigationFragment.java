package net.osmand.plus.dashboard;

import java.util.ArrayList;
import java.util.List;

import net.osmand.plus.R;
import net.osmand.plus.ContextMenuAdapter.OnContextMenuClick;
import net.osmand.plus.TargetPointsHelper.TargetPoint;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.activities.ShowRouteInfoActivity;
import net.osmand.plus.routing.RoutingHelper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 */
public class DashNavigationFragment extends DashBaseFragment {
	public static final String TAG = "DASH_NAVIGATION_FRAGMENT";
	List<TargetPoint> points = new ArrayList<TargetPoint>();

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.dash_common_fragment, container, false);
		((TextView) view.findViewById(R.id.fav_text)).setText(R.string.current_route);
		((TextView)view.findViewById(R.id.show_all)).setText(R.string.info_button);
		(view.findViewById(R.id.show_all)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(), ShowRouteInfoActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				view.getContext().startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public void onOpenDash() {
		setupNavigation();
	}

	public void setupNavigation() {
		View mainView = getView();
		final RoutingHelper routingHelper = getMyApplication().getRoutingHelper();
		getActivity();
		if (!routingHelper.isRouteCalculated() || 
				(!(getActivity() instanceof MapActivity))) {
			(mainView.findViewById(R.id.main_fav)).setVisibility(View.GONE);
			return;
		} else {
			(mainView.findViewById(R.id.main_fav)).setVisibility(View.VISIBLE);
		}
		final MapActivity map =  (MapActivity) getActivity();
		LinearLayout favorites = (LinearLayout) mainView.findViewById(R.id.items);
		favorites.removeAllViews();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dash_navigation, null, false);			
		TextView name = (TextView) view.findViewById(R.id.name);
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
		ImageView play = (ImageView) view.findViewById(R.id.play);
		name.setText(routingHelper.getGeneralRouteInformation());
		icon.setImageDrawable(getMyApplication().getIconsCache().getIcon(R.drawable.ic_action_start_navigation, 
				R.color.color_myloc_distance));
		cancel.setImageDrawable(getMyApplication().getIconsCache().getContentIcon(R.drawable.ic_action_remove_dark) 
				);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				map.getMapActions().stopNavigationActionConfirm();
			}
		});
		int nav;
		if(routingHelper.isFollowingMode()) {
			nav = R.string.cancel_navigation;
		} else {
			nav = R.string.cancel_route;
		} 
		cancel.setContentDescription(getString(nav));
		updatePlayButton(routingHelper, map, play);
		favorites.addView(view);
	}

	private void updatePlayButton(final RoutingHelper routingHelper, final MapActivity map, final ImageView play) {
		boolean toContinueNavigation = routingHelper.isRoutePlanningMode();
		play.setImageDrawable(getMyApplication().getIconsCache().getContentIcon(
				toContinueNavigation? R.drawable.ic_play_dark : R.drawable.ic_action_rec_stop ) 
				);
		play.setContentDescription(getString(toContinueNavigation ? R.string.continue_navigation :
			R.string.pause_navigation));
		play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(routingHelper.isRoutePlanningMode()) {
					routingHelper.setRoutePlanningMode(false);
					routingHelper.setFollowingMode(true);
				} else {
					routingHelper.setRoutePlanningMode(true);
					routingHelper.setFollowingMode(false);
					routingHelper.setPauseNaviation(true);
				}
				updatePlayButton(routingHelper, map, play);
				map.getMapViewTrackingUtilities().switchToRoutePlanningMode();
				map.refreshMap();
			}
		});
	}

}
