using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.ObjectModel;

namespace Person.DBUtils
{
    class PersonsModel
    {

        public string Fio { get; set; }
        public string EmployeDate { get; set; }
        public string UnemployeDate { get; set; }
        public string Status { get; set; }
        public string Deps { get; set; }
        public string Post { get; set; }
        

        public PersonsModel(string Fio, string EmployeDate, string UnemployeDate, string Status, string Deps, string Post)
        {
            this.Fio = Fio;
            this.EmployeDate = EmployeDate;
            this.UnemployeDate = UnemployeDate;
            this.Status = Status;
            this.Deps = Deps;
            this.Post = Post;
            if (this.EmployeDate != "" )
                this.EmployeDate = DateTime.Parse(this.EmployeDate).ToShortDateString();
            if(this.UnemployeDate != "")
                this.UnemployeDate = DateTime.Parse(this.UnemployeDate).ToShortDateString();

        }

        public ObservableCollection<PersonsModel> people
        {
            get;
            set;
        }

    }
}
