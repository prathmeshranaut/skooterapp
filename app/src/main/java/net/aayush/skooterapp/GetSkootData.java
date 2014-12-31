package net.aayush.skooterapp;

public class GetSkootData extends GetRawData {

    private String  mRawUrl;
    public GetSkootData(String mRawUrl) {
        super(mRawUrl);
        this.mRawUrl = mRawUrl;
    }

    public void execute()
    {
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        downloadJsonData.execute(mRawUrl);
    }

    public void processResult()
    {
        if(getmDownloadStatus() != DownloadStatus.OK)
        {

        }
    }

    public class DownloadJsonData extends DownloadRawData {
        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] par = {mRawUrl};
            return super.doInBackground(par);
        }
    }
}
