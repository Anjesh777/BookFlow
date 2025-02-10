
export interface ResourceUsage {
    storage: {
      used: number;
      total: number;
      percentage: number;
    };
    memory: {
      used: number;
      total: number;
      percentage: number;
    };
    cpu: {
      percentage: number;
    };
  }