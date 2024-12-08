import { Component, OnInit } from '@angular/core';

import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';

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
  selector: 'pros-hier-view',
  templateUrl: './hier-view.component.html',
  styleUrls: ['./hier-view.component.scss']
})
export class HierViewComponent implements OnInit {
  ngOnInit(): void {}

  treeData: TreeNode[] = [
    {
      name: 'Parent 1',
      property1: 'P1-Prop1',
      property2: 'P1-Prop2',
      children: [
        {
          name: 'Child 1.1',
          property1: 'C1.1-Prop1',
          property2: 'C1.1-Prop2',
          children: [{ name: 'Sub-child 1.1.1', property1: 'SC1.1.1-Prop1', property2: 'SC1.1.1-Prop2' }]
        },
        { name: 'Child 1.2', property1: 'C1.2-Prop1', property2: 'C1.2-Prop2' }
      ]
    },
    {
      name: 'Parent 2',
      property1: 'P2-Prop1',
      property2: 'P2-Prop2',
      children: [
        {
          name: 'Child 1.1',
          property1: 'C1.1-Prop1',
          property2: 'C1.1-Prop2',
          children: [
            {
              name: 'Sub-child 1.1.1',
              property1: 'SC1.1.1-Prop1',
              property2: 'SC1.1.1-Prop2',
              children: [
                {
                  name: 'Child 1.1',
                  property1: 'C1.1-Prop1',
                  property2: 'C1.1-Prop2',
                  children: [{ name: 'Sub-child 1.1.1', property1: 'SC1.1.1-Prop1', property2: 'SC1.1.1-Prop2' }]
                },
                {
                  name: 'Child 1.2',
                  property1: 'C1.2-Prop1',
                  property2: 'C1.2-Prop2',
                  children: [
                    {
                      name: 'Child 1.1',
                      property1: 'C1.1-Prop1',
                      property2: 'C1.1-Prop2',
                      children: [{ name: 'Sub-child 1.1.1', property1: 'SC1.1.1-Prop1', property2: 'SC1.1.1-Prop2' }]
                    },
                    { name: 'Child 1.2', property1: 'C1.2-Prop1', property2: 'C1.2-Prop2' }
                  ]
                }
              ]
            }
          ]
        },
        { name: 'Child 1.2', property1: 'C1.2-Prop1', property2: 'C1.2-Prop2' }
      ]
    }
  ];

  treeControl: FlatTreeControl<FlatNode>;
  treeFlattener: MatTreeFlattener<TreeNode, FlatNode>;
  dataSource: MatTreeFlatDataSource<TreeNode, FlatNode>;

  constructor() {
    this.treeFlattener = new MatTreeFlattener(
      this.transformNode,
      (node) => node.level,
      (node) => node.expandable,
      (node) => node.children
    );

    this.treeControl = new FlatTreeControl<FlatNode>(
      (node) => node.level,
      (node) => node.expandable
    );

    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.dataSource.data = this.treeData;
  }

  transformNode(node: TreeNode, level: number): FlatNode {
    return {
      name: node.name,
      level,
      expandable: !!node.children?.length,
      property1: node.property1,
      property2: node.property2
    };
  }

  hasChild = (_: number, node: FlatNode) => node.expandable;
}
