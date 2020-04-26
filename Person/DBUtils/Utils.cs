using System;
using MySql.Data.MySqlClient;

namespace Person.DBUtils
{
    class Utils
    {
        private static MySqlConnection GetDBConnection(string host, int port,
            string database, string username, string password)
        {
            // Connection String.
            String connString = "Server=" + host + ";Database=" + database
                + ";port=" + port + ";User Id=" + username + ";password=" + password;

            MySqlConnection conn = new MySqlConnection(connString);

            return conn;
        }

        internal void checkConnection(Func<string> toString, object parseInt, string text)
        {
            throw new NotImplementedException();
        }

        public static MySqlConnection GetDBConnection()
        {
            string host = "127.0.0.1";
            int port = 3306;
            string database = "dbo";
            string username = "root";
            string password = "PestToxic_7898";

            return GetDBConnection(host, port, database, username, password);
        }

        public void checkConnection(string host, int port, string database, string username, string password)
        {
            using (MySqlConnection conn = GetDBConnection(host, port, database, username, password))
            {
                try
                {
                    conn.Open();
                    conn.Close();

                }
                catch (Exception)
                {

                }


            }
        }
    }
}
