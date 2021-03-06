package de.xikolo.presenters.course;

import de.xikolo.jobs.base.RequestJobCallback;
import de.xikolo.managers.CourseManager;
import de.xikolo.models.Course;
import de.xikolo.presenters.base.LoadingStatePresenter;
import io.realm.Realm;
import io.realm.RealmChangeListener;

import static de.xikolo.jobs.base.RequestJobCallback.ErrorCode.NO_NETWORK;

public class CourseDetailsPresenter extends LoadingStatePresenter<CourseDetailsView> {

    public static final String TAG = CourseDetailsPresenter.class.getSimpleName();

    private CourseManager courseManager;

    private Realm realm;

    private Course coursePromise;

    private String courseId;

    private Course course;

    CourseDetailsPresenter(String courseId) {
        this.courseManager = new CourseManager();
        this.realm = Realm.getDefaultInstance();
        this.courseId = courseId;
    }

    @Override
    public void onRefresh() {
        requestCourse(true);
    }

    @Override
    public void onViewAttached(CourseDetailsView v) {
        super.onViewAttached(v);

        if (course == null) {
            requestCourse(false);
        }

        coursePromise = courseManager.getCourse(courseId, realm, new RealmChangeListener<Course>() {
            @Override
            public void onChange(Course c) {
                course = c;
                getViewOrThrow().showContent();
                getViewOrThrow().setupView(course);
            }
        });
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();

        if (coursePromise != null) {
            coursePromise.removeAllChangeListeners();
        }
    }

    @Override
    public void onDestroyed() {
        this.realm.close();
    }

    public void enroll() {
        getViewOrThrow().showBlockingProgress();

        courseManager.createEnrollment(courseId, new RequestJobCallback() {
            @Override
            public void onSuccess() {
                if (getView() != null) {
                    getView().hideProgress();
                    Course course = Course.get(courseId);
                    if (course.accessible) {
                        getView().enterCourse(courseId);
                    } else {
                        getView().hideEnrollButton();
                        getView().showCourseNotAccessibleToast();
                    }
                }
            }

            @Override
            public void onError(ErrorCode code) {
                if (getView() != null) {
                    getView().hideProgress();
                    if (code == NO_NETWORK) {
                        getView().showNetworkRequiredMessage();
                    } else if (code == ErrorCode.NO_AUTH) {
                        getView().showLoginRequiredMessage();
                        getView().openLogin();
                    }
                }
            }
        });
    }

    private void requestCourse(boolean userRequest) {
        if (getView() != null) {
            getView().showProgress();
        }
        courseManager.requestCourse(courseId, getDefaultJobCallback(userRequest));
    }

}
