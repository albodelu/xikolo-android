package de.xikolo.model.jobs;

public interface OnJobResponseListener<T> {

    public void onResponse(final T response);

    public void onCancel();

}