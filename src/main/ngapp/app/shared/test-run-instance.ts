import {TestCase} from './test-case';
import {Host} from './host';

export class TestRunInstance {

    constructor(public id: number, public testCase: TestCase, public host: Host)
    {}
}