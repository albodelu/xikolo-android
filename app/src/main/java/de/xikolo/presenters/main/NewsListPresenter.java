package de.xikolo.presenters.main;

import java.util.ArrayList;
import java.util.List;

import de.xikolo.managers.AnnouncementManager;
import de.xikolo.models.Announcement;
import de.xikolo.presenters.base.LoadingStatePresenter;
import de.xikolo.utils.LanalyticsUtil;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class NewsListPresenter extends LoadingStatePresenter<NewsListView> {

    private String courseId;

    private AnnouncementManager announcementManager;

    private Realm realm;

    private List<Announcement> announcementList;

    private RealmResults announcementListPromise;

    NewsListPresenter(String courseId) {
        this.courseId = courseId;
        this.announcementManager = new AnnouncementManager();
        this.realm = Realm.getDefaultInstance();
        this.announcementList = new ArrayList<>();
    }

    @Override
    public void onViewAttached(NewsListView view) {
        super.onViewAttached(view);

        if (announcementList == null || announcementList.size() == 0) {
            requestAnnouncements(false);
        }

        if (courseId == null) {
            this.announcementListPromise = announcementManager.listGlobalAnnouncements(realm, getAnnouncementListRealmChangeLictener());
        } else {
            this.announcementListPromise = announcementManager.listCourseAnnouncements(courseId, realm, getAnnouncementListRealmChangeLictener());
        }
    }

    private RealmChangeListener<RealmResults<Announcement>> getAnnouncementListRealmChangeLictener() {
        return new RealmChangeListener<RealmResults<Announcement>>() {
            @Override
            public void onChange(RealmResults<Announcement> results) {
                if (results.size() > 0) {
                    announcementList = results;
                    getViewOrThrow().showContent();
                    getViewOrThrow().showAnnouncementList(announcementList);
                }
            }
        };
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();

        if (announcementListPromise != null) {
            announcementListPromise.removeAllChangeListeners();
        }
    }

    @Override
    public void onDestroyed() {
        this.realm.close();
    }

    @Override
    public void onRefresh() {
        requestAnnouncements(true);
    }

    public void onAnnouncementClicked(String announcementId) {
        getViewOrThrow().openAnnouncement(announcementId);
        LanalyticsUtil.trackVisitedAnnouncementDetail(announcementId);
    }

    private void requestAnnouncements(boolean userRequest) {
        if (getView() != null) {
            getView().showProgress();
        }
        if (courseId == null) {
            announcementManager.requestGlobalAnnouncementList(getDefaultJobCallback(userRequest));
        } else {
            announcementManager.requestCourseAnnouncementList(courseId, getDefaultJobCallback(userRequest));
        }
    }

}
