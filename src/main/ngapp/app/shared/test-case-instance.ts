import {TestCase} from './test-case';

export class TestCaseInstance {

    constructor(public id: number, 
                public name: string, 
                public description: string,
                public testCase: TestCase) 
    {}
}