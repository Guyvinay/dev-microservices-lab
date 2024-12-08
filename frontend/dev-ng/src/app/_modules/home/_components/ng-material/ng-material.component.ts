import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { catchError, of } from "rxjs";
import { Tab, User } from "src/app/_models/models";
import { CustomDataSource } from "src/app/_services/CustomeDataSource";

interface TreeNode {
  name: string;
  property1?: string;
  property2?: string;
  children?: TreeNode[];
}

interface FlatNode {
  name: string;
  level: number;
  expandable: boolean;
  property1?: string;
  property2?: string;
}

@Component({
  selector: "app-ng-material",
  templateUrl: "./ng-material.component.html",
  styleUrls: ["./ng-material.component.scss"],
})
export class NgMaterialComponent implements OnInit {
  displayedColumns: string[] = [];
  tabs: Tab[];

  dataSource: CustomDataSource;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    // this.loadData(0);
    // this.loadTabsData();
  }
  loadTabsData() {
    this.http
      .get<Tab[]>("https://jsonplaceholder.typicode.com/tabs")
      .pipe(
        catchError(() => {
          const tabs: Tab[] = [
            {
              order: 3,
              label: "Tab 1",
              content: "tab 1",
            },
            {
              order: 1,
              label: "Tab 2",
              content: "tab 3",
            },
            {
              order: 2,
              label: "Tab 3",
              content: "tab 3",
            },
          ];
          return of(tabs);
        })
      )
      .subscribe((response: Tab[]) => {
        this.tabs = response.sort((a, b)=> a.order - b.order);
      });
  }
  loadData(tabIndex: number): void {
    const apiEndpoints = [
      "https://jsonplaceholder.typicode.com/users",
      "https://jsonplaceholder.typicode.com/posts", // another data set
      "https://jsonplaceholder.typicode.com/comments", // another data set
    ];

    this.http.get<User[]>(apiEndpoints[tabIndex]).subscribe((data) => {
      this.dataSource = new CustomDataSource(data);
      // Extract columns from the first object
      if (data.length > 0) {
        this.displayedColumns = Object.keys(data[0]);
      }
    });
  }

  focusChangeEvent($event: any) {
    console.log($event);
  }

  // Sample flow data (you can replace this with actual data from a service)
  flowData = {
    flows: [
      { flowId: 'flow1', flowDesc: 'Flow 1' },
      { flowId: 'flow2', flowDesc: 'Flow 2' }
    ]
  };

  isLoading = false; // Show skeleton while loading

  // Method to handle flow selection
  openFlow(flowId: string) {
    console.log(`Opening flow: ${flowId}`);
    // Add your flow opening logic here
  }
}
